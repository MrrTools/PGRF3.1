#version 330

in vec2 inPosition;

uniform mat4 uView;
uniform mat4 uProj;

void main() {
    vec2 newPos = inPosition * 2 - 1;
    float z = 0.5 * cos(sqrt(20 * pow(newPos.x, 2) + 20 * pow(newPos.y, 2)));
    gl_Position = uProj * uView * vec4(newPos, z, 1.0);
}
