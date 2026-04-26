import Image from "next/image";

function Logo({ showText = false }) {
  return (
    <>
      <div className="flex h-[40px] w-[40px] items-center justify-center rounded-sm dark:bg-white">
        <Image src="/logo.png" alt="logo-picture" width={32} height={32} />
      </div>
      {showText && (
        <span className="text-primary-dark text-lg font-semibold">MedAI</span>
      )}
    </>
  );
}

export default Logo;
