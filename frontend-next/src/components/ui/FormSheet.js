"use client";

import { cloneElement, useState } from "react";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/shadcn/sheet";

function FormSheet({ title, description, form, children, className = "" }) {
  const [sheetOpen, setSheetOpen] = useState(false);

  return (
    <Sheet open={sheetOpen} onOpenChange={setSheetOpen}>
      <SheetTrigger asChild>{children}</SheetTrigger>
      <SheetContent
        className={`flex flex-col gap-0 data-[side=right]:w-full data-[side=right]:sm:max-w-xl ${className}`}
      >
        <SheetHeader className="border-border shrink-0 border-b pb-4">
          <SheetTitle className="capitalize">{title}</SheetTitle>
          {description && <SheetDescription>{description}</SheetDescription>}
        </SheetHeader>

        {/* Inject close sheet in form */}

        {sheetOpen &&
          cloneElement(form, { closeSheet: () => setSheetOpen(false) })}
      </SheetContent>
    </Sheet>
  );
}

export default FormSheet;
