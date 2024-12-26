package website

import p5.Sketch
import p5.core.Font
import p5.core.P5

fun front() = Sketch {

    Preload {
        loadFont("./fonts/futuralight.otf")
    }

    Setup {

        noCanvas()

        val links = listOf("Home", "Art Projects", "Other Projects", "About Me")


        Layout {

            getBody().style("margin", "0px")

            GridStyle(false) {
                style("width", "100%")
                style("height", "100%")
            }

            ItemStyle {
                style("font-family", "futuralight")
            }

            Row {

                GridStyle(false) {
                    style("height", "100%")
                }

                // Sidebar
                Row {

                    GridStyle(false) {
                        style("position", "sticky")
                        style("height", "100vh")
                        style("top", "0")
                        style("background-color", "#f5f6fa")
                    }

                    // Gutter
                    Column {
                        GridStyle {
                            style("width", "5vw")
                        }
                    }

                    // Menu
                    Column {

                        GridStyle(false) {
                            style("height", "100vh")
                            style("grid-template-rows", "auto 1fr auto")
                            style("width", "min-content")
                        }

                        // Header
                        add(createP("Foldster's Site")) {
                            style("align-self", "start")
                            style("height", "min-content")
                        }

                        // Links
                        Column {
                            GridStyle(false) {
                                style("padding-top", "32px")
                                style("width", "min-content")
                                style("align-self", "start")
                            }

                            for(link in links) {
                                val p = createP(link)
                                Stack {
                                    GridStyle {
                                        style("zoom", "0.8")
                                        style("white-space", "nowrap")
                                        style("line-height", "0px")
                                        style("padding-right", "5vw")
                                        style("padding-left", "2vw")
                                        style("box-sizing", "border-box")
                                        style("width", "100%")
                                        style("height", "32px")
                                        style("cursor", "pointer")
                                    }
                                    container.mouseOver {
                                        GridStyle {
                                            style("background-color", "#eceef5")
                                        }
                                        render(false)
                                    }
                                    container.mouseOut {
                                        GridStyle {
                                            style("background-color", "#00000000")
                                        }
                                        render(false)
                                    }
                                    add(p)
                                }


                            }

                            //addAll(links.map { createP(it) })
                        }
                        // Footer

                        add(createP("This is a really neat footer.<br>Foldster's Projects &copy2022")) {
                            style("align-self", "end")
                            style("zoom", "0.5")
                            style("word-wrap", "normal")
                            style("width", "50%")
                            style("height", "100%")
                        }

                    }



                }
                // Main

                Column {
                    GridStyle {
                        style("width", "100%")
                    }
                    add(createP("Hello <br>".repeat(200)))
                }
            }
        }
    }
}