"use client";

import { Inter } from "next/font/google";
import ErrorState from "@/components/ui/ErrorState";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export default function GlobalError({ error, reset }) {
  return (
    <html lang="en">
      <body className={`${inter.className} antialiased`}>
        <div className="flex min-h-screen items-center justify-center">
          <ErrorState
            title="Something went wrong"
            description={
              error?.message ||
              "An unexpected error occurred. Please try again."
            }
            onRetry={() => reset()}
          />
        </div>
      </body>
    </html>
  );
}
