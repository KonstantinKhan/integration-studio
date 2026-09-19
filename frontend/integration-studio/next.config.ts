import path from "node:path";
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Pin the workspace root so Turbopack does not infer it from stray
  // lockfiles in parent directories (breaks tailwindcss resolution).
  turbopack: {
    root: path.join(__dirname),
  },
};

export default nextConfig;
