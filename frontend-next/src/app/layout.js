import { Inter } from "next/font/google";
import { Toaster } from "react-hot-toast";
import { ThemeProvider } from "next-themes";
import QueryProvider from "@/lib/providers/query-provider";
import "./globals.css";

const inter = Inter({
  subsets: ["latin"],
});

export const metadata = {
  title: {
    template: "%s | MedAI",
    default: "MedAI",
  },
  description:
    "Medical platform helps you find your suitable doctor with some AI help.",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${inter.className} antialiased`}>
        <ThemeProvider
          attribute="class"
          enableSystem
          disableTransitionOnChange
          defaultTheme="system"
        >
          <QueryProvider>{children}</QueryProvider>
          <Toaster
            toastOptions={{
              className:
                "!bg-surface !text-text-base !border !border-border !shadow-md",
              success: { duration: 3000 },
              error: { duration: 5000 },
            }}
          />
        </ThemeProvider>
      </body>
    </html>
  );
}
