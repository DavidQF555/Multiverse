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
    vec3(0.077101, 0.077101, 0.077101),
    vec3(0.065767, 0.065767, 0.065767),
    vec3(0.076550, 0.076550, 0.076550),
    vec3(0.090428, 0.090428, 0.090428),
    vec3(0.093262, 0.093262, 0.093262),
    vec3(0.091434, 0.091434, 0.091434),
    vec3(0.121063, 0.121063, 0.121063),
    vec3(0.114224, 0.114224, 0.114224),
    vec3(0.144162, 0.144162, 0.144162),
    vec3(0.131712, 0.131712, 0.131712),
    vec3(0.140125, 0.140125, 0.140125),
    vec3(0.183043, 0.183043, 0.183043),
    vec3(0.184786, 0.184786, 0.184786),
    vec3(0.228196, 0.228196, 0.228196),
    vec3(0.298917, 0.298917, 0.298917),
    vec3(0.352422, 0.352422, 0.352422)
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