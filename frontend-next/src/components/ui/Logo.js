import Image from "next/image";

function Logo() {
  return (
    <>
      <Image src="/logo.png" alt="logo-picture" width={32} height={32} />
      {/* <span className="text-primary-dark text-xl font-semibold">MedAI</span> */}
    </>
  );
}

export default Logo;
