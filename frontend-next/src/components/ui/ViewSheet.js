"use client";

import { useState } from "react";
import {
  Sheet,
  SheetBody,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/shadcn/sheet";

function ViewSheet({ title, description, trigger, children, className = "" }) {
  const [sheetOpen, setSheetOpen] = useState(false);

  return (
    <Sheet open={sheetOpen} onOpenChange={setSheetOpen}>
      <SheetTrigger asChild>{trigger}</SheetTrigger>
      <SheetContent
        className={`flex flex-col gap-0 data-[side=right]:w-full data-[side=right]:sm:max-w-xl ${className}`}
      >
        <SheetHeader className="border-border shrink-0 border-b pb-4">
          <SheetTitle className="capitalize">{title}</SheetTitle>
          {description && <SheetDescription>{description}</SheetDescription>}
        </SheetHeader>

        <SheetBody>{children}</SheetBody>
      </SheetContent>
    </Sheet>
  );
}

export default ViewSheet;
