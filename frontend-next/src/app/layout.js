import { Inter } from "next/font/google";
import { Toaster } from "react-hot-toast";
import { ThemeProvider } from "next-themes";
import "./globals.css";

const inter = Inter({
  subsets: ["latin"],
});

export const metadata = {
  title: "MedAi",
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
          {children}
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
