#version 330 core

in vec2 fragCoord;
out vec4 fragColor;

uniform vec2 iResolution;

const vec4 backgroundColor = vec4(0.18, 0.18, 0.18, 1.0);
const vec4 crosshairsColor = vec4(0.8, 0.8, 0.8, 1.0);
const vec2 centerUV = vec2(0.5, 0.5);
const float thickness = 1.4;
const float length = 15.0;

// Helper functions
float gt(float x, float y) {
    return max(sign(x - y), 0.0);
}

float and_(float x, float y) {
    return x * y;
}

float or_(float x, float y) {
    return min(x + y, 1.0);
}

float not_(float x) {
    return 1.0 - x;
}

void main() {
    vec2 center = iResolution * centerUV;
    vec2 d = abs(center - fragCoord);

    float crosshairMask = or_(
        and_(gt(thickness, d.x), gt(length, d.y)),
        and_(gt(thickness, d.y), gt(length, d.x))
    );

    float backgroundMask = not_(crosshairMask);

    fragColor = mix(vec4(0.0), crosshairsColor, crosshairMask);
}
