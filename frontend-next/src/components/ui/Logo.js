import Image from "next/image";

function Logo({ showText = false }) {
  return (
    <>
      <div className="flex size-[40px] shrink-0 items-center justify-center rounded-sm dark:bg-white">
        <Image src="/logo.png" alt="logo-picture" width={32} height={32} />
      </div>
      {showText && (
        <span className="text-primary-dark text-lg font-semibold">MedAI</span>
      )}
    </>
  );
}

export default Logo;
