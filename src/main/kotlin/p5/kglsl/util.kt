package p5.kglsl

fun KGLSL.center(coord: vec2, res: vec2): vec2 {
    return (2.0*coord - res)/res.x
}


