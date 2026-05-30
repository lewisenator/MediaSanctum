import { defineConfig } from 'vitest/config';

const testConfig = defineConfig({
    test: {
        environment: 'jsdom',
        setupFiles: ['./src/test/setup.ts'],
    }
});

export default testConfig;