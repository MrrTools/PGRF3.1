#version 330
in vec2 inPosition;
out vec2 texCoord;
uniform float time;

void main() {
    texCoord = inPosition;
    vec2 newPos = inPosition * 2 - 1;
    gl_Position = vec4(newPos, 0, 1.0);
}


