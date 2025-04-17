#version 400 core

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct SpotLight {
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

uniform sampler2D backgroundTexture;
uniform sampler2D redTexture;
uniform sampler2D greenTexture;
uniform sampler2D blueTexture;
uniform sampler2D blendMap;

uniform vec3 ambientLight;
uniform Material material;
uniform float specularPower;
uniform float SIZE;
uniform DirectionalLight directionalLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;
void setupColors(Material material, vec2 textCoord) {
    if (material.hasTexture == 0) {
        vec4 blendMapColour = texture(blendMap, textCoord);
        float backgroundTextureAmt = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
        vec2 tiledCoords = textCoord * SIZE;
        vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backgroundTextureAmt;
        vec4 redTextureColour = texture(redTexture, tiledCoords) * blendMapColour.r;
        vec4 greenTextureColour = texture(greenTexture, tiledCoords) * blendMapColour.g;
        vec4 blueTextureColour = texture(blueTexture, tiledCoords) * blendMapColour.b;

        ambientC = backgroundTextureColour + redTextureColour + greenTextureColour + blueTextureColour;
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}


vec4 calcLightColour(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal){
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // diffused light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuseC * vec4(light_color, 1.0) * light_intensity * diffuseFactor;

    // specular light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflectedLight = normalize(reflect(from_light_dir, normal));
    float specularFactor = max(dot(camera_direction, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0);

    return diffuseColor + specularColor;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal){
    return calcLightColour(light.color, light.intensity, position, normalize(light.direction), normal);
}
vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 light_dir = light.position - position;
    float distance = length(light_dir);
    vec3 to_light_dir = normalize(light_dir);

    vec4 light_colour = calcLightColour(light.colour, light.intensity, position, to_light_dir, normal);

    // Attenuation calculation with clamping for stability
    float attenuation = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
    attenuation = max(attenuation, 0.001); // Prevent division by zero or too small values

    return light_colour / attenuation;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 light_dir = light.pl.position - position;
    vec3 to_light_dir = normalize(light_dir);
    vec3 from_light_dir = -to_light_dir;
    float spot_alpha = dot(from_light_dir, normalize(light.conedir));

    vec4 colour = vec4(0.0);

    if (spot_alpha > light.cutoff) {
        float factor = (spot_alpha - light.cutoff) / (1.0 - light.cutoff);
        factor = clamp(factor, 0.0, 1.0);
        colour = calcPointLight(light.pl, position, normal) * factor;
    }
    return colour;
}

void main() {
    setupColors(material, fragTextureCoord);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, fragPos, fragNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) {
            diffuseSpecularComp += calcPointLight(pointLights[i], fragPos, fragNormal);
        }
    }

    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (spotLights[i].pl.intensity > 0) {
            diffuseSpecularComp += calcSpotLight(spotLights[i], fragPos, fragNormal);
        }
    }
    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}