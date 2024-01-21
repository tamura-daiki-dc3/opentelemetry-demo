"use client";

import Footer from "../components/footer";
import { Inter } from "next/font/google";
import { initFaro } from "../utils/falo";

const inter = Inter({ subsets: ["latin"] });

if (typeof window !== "undefined") {
  // Client-side-only code
  initFaro();
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ja">
      <body className={inter.className}>
        {children}
        <hr />
        <Footer></Footer>
      </body>
    </html>
  );
}
