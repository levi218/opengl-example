varying vec3 color;
uniform float time;

void main(void)
{
    vec4 pos = gl_ModelViewProjectionMatrix * gl_Vertex;
    float x = pos.x;
    float y = pos.y;

    vec4 newVertex = vec4(x*cos(time),y*sin(x+time),pos.z,pos.w);
    color = gl_Color.rgb;
    
    gl_Position = newVertex;
}
