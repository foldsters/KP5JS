package projects.testing

import p5.NativeP5
import p5.Sketch


fun perlinNoiseTesting() = Sketch {

    Setup {



        createCanvas(1024, 1024)
        background(0)
        frameRate(60)
        stroke(255, 10)
        strokeWeight(1)
        val centerVec = createVector(0.5, 0.5)

        val table = NativeP5.Table()
        table.addColumn("value")
        table.addColumn("rawCount")
        table.addColumn("uniformCount")

        val rawCount = Array(256) {0}
        val uniformCount = Array(256) {0}

        pixelDensity(1)

//        fun uniformPerlin(x: Number, y: Number): Number {
//
//
//            val rawPerlin = noise(x, y).toDouble()
//            val uniformPerlin = smoothstep(0, 1, smoothstep(0.1, 0.82, rawPerlin))
//
//            return uniformPerlin
//
//            rawCount[(rawPerlin*255).toInt()] += 1
//            uniformCount[(uniformPerlin*255).toInt()] += 1
//
//            if (x < width/2) {
//                stroke(255*rawPerlin)
//            } else {
//                stroke(255*uniformPerlin)
//            }
//
//            point(x, y)
//
//        }

        fun smoothstep(x: Number): Number {
            return x*x*(3.0-2.0*x)
        }

        fun smoothstep(edge0: Number, edge1: Number, x: Number): Number {
            val t = ((x-edge0)/(edge1-edge0)).toDouble().coerceIn(0.0..1.0)
            return smoothstep(t)
        }

        fun uniformPerlin(x: Number, y: Number): Number {
            val rawPerlin = noise(x, y)
            return smoothstep(smoothstep(0.1, 0.82, rawPerlin))
        }

        for (x in 0 until width) {
            for (y in 0 until height) {

                val fx = 10.0*x/width.toDouble()
                val fy = 10.0*y/height.toDouble()

                val raw = noise(fx, fy)
                val uniform = uniformPerlin(fx, fy)

                if (x < width/2) {
                    stroke(255*raw)
                } else {
                    stroke(255*uniform)
                }

                point(x, y)
            }
        }
//
//        for(i in 0..255) {
//            val row = table.addRow()
//            row.setNum("value", i)
//            row.setNum("rawCount", rawCount[i])
//            row.setNum("uniformCount", uniformCount[i])
//        }
//
//        saveTable(table, "perlinCount", P5.TableMode.CSV)


    }

}