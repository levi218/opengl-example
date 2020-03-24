varying vec3  normal, color, pos;
void main(void)
{
    vec4 original    =  gl_Vertex;

    pos = vec3(gl_ModelViewMatrix * original);
    color = gl_Color.rgb;
    vec3 vertex_normal = gl_Normal.xyz;
    normal       =  vec3(normalize(gl_ModelViewMatrix * vec4(vertex_normal,0)));

    gl_Position =  gl_ModelViewProjectionMatrix *original;
}
