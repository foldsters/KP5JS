export interface KotlinConfig {
  moduleName: string
  kotlinVersion: string
  npmDependencies?: Record<string, string>
  kotlinDependencies?: string[]
  compiler?: {
    target?: string
    generateTypeScript?: boolean
  }
  optIns?: string[]
}

export function defineConfig(config: KotlinConfig): KotlinConfig {
  return config
}
