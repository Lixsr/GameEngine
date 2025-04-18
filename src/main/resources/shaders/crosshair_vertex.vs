#version 330 core

layout (location = 0) in vec2 position;

out vec2 fragCoord;

uniform vec2 iResolution;

void main() {
    // Convert from NDC [-1,1] to pixel space [0, iResolution]
    fragCoord = (position * 0.5 + 0.5) * iResolution;
    gl_Position = vec4(position, 0.0, 1.0);
}
