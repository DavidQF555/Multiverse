#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec4 ColorModulator;

uniform float GameTime;
uniform int EndPortalLayers;

in vec4 texProj0;
in vec4 vertexColor;

const vec3[] COLORS = vec3[](
    vec3(0.838550, 0.838550, 0.838550),
    vec3(0.832883, 0.832883, 0.832883),
    vec3(0.838275, 0.838275, 0.838275),
    vec3(0.845214, 0.845214, 0.845214),
    vec3(0.846631, 0.846631, 0.846631),
    vec3(0.845717, 0.845717, 0.845717),
    vec3(0.860531, 0.860531, 0.860531),
    vec3(0.857112, 0.857112, 0.857112),
    vec3(0.872081, 0.872081, 0.872081),
    vec3(0.865856, 0.865856, 0.865856),
    vec3(0.870062, 0.870062, 0.870062),
    vec3(0.891521, 0.891521, 0.891521),
    vec3(0.892393, 0.892393, 0.892393),
    vec3(0.914098, 0.914098, 0.914098),
    vec3(0.949458, 0.949458, 0.949458),
    vec3(0.976211, 0.976211, 0.976211)
);

const mat4 SCALE_TRANSLATE = mat4(
    0.5, 0.0, 0.0, 0.25,
    0.0, 0.5, 0.0, 0.25,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
);

mat4 end_portal_layer(float layer) {
    mat4 translate = mat4(
        1.0, 0.0, 0.0, 17.0 / layer,
        0.0, 1.0, 0.0, (2.0 + layer / 1.5) * (GameTime * 1.5),
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    );
    mat2 rotate = mat2_rotate_z(radians((layer * layer * 4321.0 + layer * 9.0) * 2.0));
    mat2 scale = mat2((4.5 - layer / 4.0) * 2.0);
    return mat4(scale * rotate) * translate * SCALE_TRANSLATE;
}

out vec4 fragColor;

void main() {
    vec3 color = textureProj(Sampler0, texProj0).rgb * COLORS[0];
    for (int i = 0; i < EndPortalLayers; i++) {
        color += textureProj(Sampler1, texProj0 * end_portal_layer(float(i + 1))).rgb * COLORS[i];
    }
    fragColor = vec4(color, 1.0) * vertexColor * ColorModulator;
}