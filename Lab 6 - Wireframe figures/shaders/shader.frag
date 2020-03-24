
varying vec3 normal, color, pos;  
uniform vec3 cameraPos;
void main(void)
{
    vec3 neye = normalize(cameraPos-pos);
    vec3 nnormal = normalize(normal);

    if(dot(neye,nnormal)>0)
        gl_FragColor = vec4(color,1.0);
    else 
        discard;
}
