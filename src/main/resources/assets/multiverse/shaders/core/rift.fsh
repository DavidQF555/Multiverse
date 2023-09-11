#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec4 ColorModulator;

uniform float GameTime;
uniform int RiftLayers;

in vec4 texProj0;
in vec4 vertexColor;

const vec3[] COLORS = vec3[](
    vec3(0.8, 0.8, 0.8),
    vec3(0.45, 0.45, 0.45),
    vec3(0.5, 0.5, 0.5),
    vec3(0.55, 0.55, 0.55),
    vec3(0.6, 0.6, 0.6),
    vec3(0.65, 0.65, 0.65),
    vec3(0.7, 0.7, 0.7),
    vec3(0.75, 0.75, 0.75),
    vec3(0.8, 0.8, 0.8)
);

const mat4 SCALE_TRANSLATE = mat4(
    0.5, 0.0, 0.0, 0.25,
    0.0, 0.5, 0.0, 0.25,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
);

mat4 rift_layer(float layer) {
    mat4 translate = mat4(
        1.0, 0.0, 0.0, 8.5 / layer,
        0.0, 1.0, 0.0, (2.0 + layer * 4.0 / 3.0) * (GameTime * 1.5),
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    );
    mat2 rotate = mat2_rotate_z(radians((layer * layer * 17284.0 + layer * 18.0) * 2.0));
    mat2 scale = mat2((layer * layer - 16.0 * layer + 64) / 14.0 + 0.5);
    return mat4(scale * rotate) * translate * SCALE_TRANSLATE;
}

out vec4 fragColor;

void main() {
    vec3 color = textureProj(Sampler0, texProj0).rgb * COLORS[0];
    for (int i = 1; i <= RiftLayers; i++) {
        color += textureProj(Sampler1, texProj0 * rift_layer(float(i))).rgb * COLORS[i];
    }
    fragColor = vec4(color, 1.0) * vertexColor * ColorModulator;
}