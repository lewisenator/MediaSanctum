import { defineConfig } from 'vitest/config';

const testConfig = defineConfig({
    test: {
        environment: 'jsdom'
    }
});

export default testConfig