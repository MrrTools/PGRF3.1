#version 330

in vec2 texCoord;

uniform sampler2D textureScene;

out vec4 outColor;

void main() {
    outColor = texture(textureScene, texCoord);


}

