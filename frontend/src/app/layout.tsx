"use client";

import Footer from "../components/footer";
import { Inter } from "next/font/google";
import { initFaro } from "../utils/falo";

import { AppRouterCacheProvider } from "@mui/material-nextjs/v14-appRouter";
import { styled, createTheme, ThemeProvider } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";

const inter = Inter({ subsets: ["latin"] });

const defaultTheme = createTheme();

if (typeof window !== "undefined") {
  // Client-side-only code
  initFaro();
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ja">
      <body className={inter.className}>
        <AppRouterCacheProvider options={{ enableCssLayer: true }}>
          <ThemeProvider theme={defaultTheme}>
            {/* CssBaseline kickstart an elegant, consistent, and simple baseline to build upon. */}
            <CssBaseline />
            {children}
            <hr />
            <Footer></Footer>
          </ThemeProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}
