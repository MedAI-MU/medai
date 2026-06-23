"use client";

import * as DialogPrimitive from "@radix-ui/react-dialog";

import { cn } from "@/lib/utils";
import Button from "@/components/ui/Button";
import { XIcon } from "lucide-react";
import ButtonIcon from "../ui/ButtonIcon";

function Dialog({ ...props }) {
  return <DialogPrimitive.Root data-slot="dialog" {...props} />;
}

function DialogTrigger({ ...props }) {
  return <DialogPrimitive.Trigger data-slot="dialog-trigger" {...props} />;
}

function DialogPortal({ ...props }) {
  return <DialogPrimitive.Portal data-slot="dialog-portal" {...props} />;
}

function DialogClose({ ...props }) {
  return <DialogPrimitive.Close data-slot="dialog-close" {...props} />;
}

function DialogOverlay({ className, ...props }) {
  return (
    <DialogPrimitive.Overlay
      data-slot="dialog-overlay"
      className={cn(
        "data-[state=open]:animate-in data-[state=open]:fade-in-0 data-[state=closed]:animate-out data-[state=closed]:fade-out-0 fixed inset-0 isolate z-50 bg-black/50 duration-100 supports-backdrop-filter:backdrop-blur-xs",
        className,
      )}
      {...props}
    />
  );
}

function DialogContent({
  className,
  children,
  showCloseButton = true,
  ...props
}) {
  return (
    <DialogPortal>
      <DialogOverlay />
      <DialogPrimitive.Content
        data-slot="dialog-content"
        className={cn(
          "bg-surface text-text-base border-border max-sm:animate-in max-sm:slide-in-from-bottom-full max-sm:data-[state=closed]:animate-out max-sm:data-[state=closed]:slide-out-to-bottom-full sm:data-[state=open]:animate-in sm:data-[state=open]:fade-in-0 sm:data-[state=open]:zoom-in-95 sm:data-[state=closed]:animate-out sm:data-[state=closed]:fade-out-0 sm:data-[state=closed]:zoom-out-95 fixed z-50 grid w-full gap-4 rounded-t-xl border p-6 shadow-lg duration-100 outline-none max-sm:bottom-0 sm:top-1/2 sm:left-1/2 sm:max-w-lg sm:-translate-x-1/2 sm:-translate-y-1/2 sm:rounded-b-xl",
          className,
        )}
        {...props}
      >
        {children}
        {showCloseButton && (
          <DialogPrimitive.Close asChild>
            <ButtonIcon className="absolute top-4 right-4">
              <XIcon size={20} />
            </ButtonIcon>
          </DialogPrimitive.Close>
        )}
      </DialogPrimitive.Content>
    </DialogPortal>
  );
}

function DialogHeader({ className, ...props }) {
  return (
    <div
      data-slot="dialog-header"
      className={cn(
        "flex flex-col gap-1.5 text-center sm:text-start",
        className,
      )}
      {...props}
    />
  );
}

function DialogFooter({
  className,
  showCloseButton = false,
  disableCloseButton = false,
  children,
  ...props
}) {
  return (
    <div
      data-slot="dialog-footer"
      className={cn(
        "bg-surface-overlay border-border relative z-10 -mx-6 mt-2 -mb-6 flex flex-row justify-end gap-2 border-t p-4 sm:rounded-b-xl",
        className,
      )}
      {...props}
    >
      {showCloseButton && (
        <DialogPrimitive.Close asChild>
          <Button variation="ghost" disabled={disableCloseButton}>
            Close
          </Button>
        </DialogPrimitive.Close>
      )}
      {children}
    </div>
  );
}

function DialogTitle({ className, ...props }) {
  return (
    <DialogPrimitive.Title
      data-slot="dialog-title"
      className={cn(
        "text-text-base text-lg leading-none font-semibold tracking-tight",
        className,
      )}
      {...props}
    />
  );
}

function DialogDescription({ className, ...props }) {
  return (
    <DialogPrimitive.Description
      data-slot="dialog-description"
      className={cn("text-text-muted text-sm", className)}
      {...props}
    />
  );
}

export {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogOverlay,
  DialogPortal,
  DialogTitle,
  DialogTrigger,
};
