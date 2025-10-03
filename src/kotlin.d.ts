// TypeScript definitions for Kotlin/JS compiled output
// Auto-generated from .kotlin-build/build/js/packages/kp5js/kotlin/kp5js.d.ts
// DO NOT EDIT - regenerate with: npm run generate:types

declare module '@kotlin/kp5js.mjs' {
type Nullable<T> = T | null | undefined
export declare function SimpleSketch(width: any/* Number */, height: any/* Number */, loop: boolean | undefined, onDraw: (p0: any/* P5 */, p1: number) => void): Sketch;
export declare class Sketch {
    private constructor();
    static SketchConstructor(sketch: (p0: Sketch) => void): Sketch;
    get p5(): any/* P5 */;
    set p5(value: any/* P5 */);
    Preload(block: (p0: any/* P5 */) => void): void;
    Setup(block: (p0: any/* P5 */) => void): void;
    WindowResized(block: (p0: any/* P5 */) => void): void;
    DeviceMoved(block: (p0: any/* P5 */) => void): void;
    DeviceTurned(block: (p0: any/* P5 */) => void): void;
    DeviceShaken(block: (p0: any/* P5 */) => void): void;
    MouseMoved(block: (p0: any/* P5 */) => void): void;
    MouseDragged(block: (p0: any/* P5 */) => void): void;
    MousePressed(block: (p0: any/* P5 */) => void): void;
    MouseReleased(block: (p0: any/* P5 */) => void): void;
    MouseClicked(block: (p0: any/* P5 */) => void): void;
    DoubleClicked(block: (p0: any/* P5 */) => void): void;
    TouchStarted(block: (p0: any/* P5 */) => void): void;
    TouchMoved(block: (p0: any/* P5 */) => void): void;
    TouchEnded(block: (p0: any/* P5 */) => void): void;
    KeyPressed(block: (p0: any/* KeyboardEvent */) => void): void;
    KeyReleased(block: (p0: any/* KeyboardEvent */) => void): void;
    KeyTyped(block: (p0: any/* KeyboardEvent */) => void): void;
    MouseWheel(block: (p0: any/* WheelEvent */) => void): void;
    Layout(block: (p0: any/* P5.Grid */) => void): void;
    updateLayout(): void;
    getAutoStepsPerFrame(): number;
    autoAdjustSteps(block: (p0: number) => void): void;
    DrawAutostart(stepsPerFrame: number | undefined, autoStart: boolean | undefined, block: (p0: any/* P5 */, p1: number) => void): Sketch.DrawContinuation;
    Draw(stepsPerFrame: any/* typeof AUTO */, block: (p0: any/* P5 */, p1: number) => void): Sketch.DrawContinuation;
    DrawWithPixelsAutostart(stepsPerFrame: number | undefined, autoStart: boolean | undefined, block: (p0: any/* P5.PixelScope */, p1: number) => void): Sketch.DrawContinuation;
    DrawWithPixels(stepsPerFrame: any/* typeof AUTO */, block: (p0: any/* P5.PixelScope */, p1: number) => void): Sketch.DrawContinuation;
    DrawWhileAutostart(cond: () => boolean, stepsPerFrame: number | undefined, block: (p0: any/* P5 */, p1: number) => void): Sketch.DrawContinuation;
    DrawWhile(cond: () => boolean, stepsPerFrame: any/* typeof AUTO */, block: (p0: any/* P5 */, p1: number) => void): Sketch.DrawContinuation;
    DrawForAutostart<T>(iter: any/* Iterable<T> */, stepsPerFrame: number | undefined, loop: boolean | undefined, block: (p0: any/* P5 */, p1: T) => void): Sketch.DrawContinuation;
    DrawFor<T>(iter: any/* Iterable<T> */, stepsPerFrame: any/* typeof AUTO */, block: (p0: any/* P5 */, p1: T) => void): Sketch.DrawContinuation;
    DrawForWithPixelsAutostart<T>(iter: any/* Iterable<T> */, stepsPerFrame: number | undefined, block: (p0: any/* P5.PixelScope */, p1: T) => void): Sketch.DrawContinuation;
    DrawForWithPixels<T>(iter: any/* Iterable<T> */, stepsPerFrame: any/* typeof AUTO */, block: (p0: any/* P5.PixelScope */, p1: T) => void): Sketch.DrawContinuation;
    DrawWhileWithPixelsAutostart(cond: () => boolean, stepsPerFrame: number | undefined, block: (p0: any/* P5.PixelScope */) => void): Sketch.DrawContinuation;
    DrawWhileWithPixels(cond: () => boolean, stepsPerFrame: any/* typeof AUTO */, block: (p0: any/* P5.PixelScope */) => void): Sketch.DrawContinuation;
    DrawUsingAutostart<T>(frames: Nullable<number> | undefined, stepsPerFrame: number | undefined, _with: T, using: (p0: () => void) => void, block: (p0: T) => void): Sketch.DrawContinuation;
    DrawUsing<T>(frames: Nullable<number> | undefined, stepsPerFrame: any/* typeof AUTO */, _with: T, using: (p0: () => void) => void, block: (p0: T) => void): Sketch.DrawContinuation;
}
export declare namespace Sketch {
    class DrawContinuation {
        constructor();
        get afterFrame(): Nullable<() => void>;
        set afterFrame(value: Nullable<() => void>);
        get afterDone(): Nullable<() => void>;
        set afterDone(value: Nullable<() => void>);
        AfterFrame(continuation: () => void): Sketch.DrawContinuation;
        AfterDone(continuation: () => void): Sketch.DrawContinuation;
    }
}
export declare class CarbonProps {
    constructor(filename: string, scaleFactor: number, imageFactor: number);
    get filename(): string;
    get scaleFactor(): number;
    get imageFactor(): number;
}
export declare function Carbon(props: CarbonProps): Sketch;
export declare function MysticTomato(): Sketch;
export declare function PerlinLightning(): Sketch;
export declare class CirclizerProps {
    constructor(sourceImage: string);
    get sourceImage(): string;
}
export declare function Circlizer(props: CirclizerProps): Sketch;
export declare class CorrelationTilerProps {
    constructor(imagePath1: string, imagePath2: string);
    get imagePath1(): string;
    get imagePath2(): string;
}
export declare function CorrelationTiler(props: CorrelationTilerProps): Sketch;
export declare function Curler(): Sketch;
export declare function Penrose(): Sketch;
export declare class HopperProps {
    constructor();
    get canvasSize(): number;
    set canvasSize(value: number);
}
export declare function Hopper(props: HopperProps): Sketch;
export declare function HopperClouds(): Sketch;
export declare function HopperClouds2(): Sketch;
export declare function Huegene(): Sketch;
export declare function HuegeneFlow(): Sketch;
export declare function Moire(): Sketch;
export declare function Oge(): Sketch;
export declare function Bitfield(): Sketch;
export declare function ExampleShader(): Sketch;
export declare function UniformBridgeExample(): Sketch;
export declare function Singularity(): Sketch;
export declare class PaletteGeneratorProps {
    constructor(imageSource: string);
    get imageSource(): string;
}
export declare function PaletteGenerator(props: PaletteGeneratorProps): Sketch;
export declare function SpectGrad(): Sketch;
}
