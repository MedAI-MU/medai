"use client";

import { Search } from "lucide-react";
import FormInput from "./FormInput";
import { useRef, useState } from "react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";

function SearchBar({ queryKey, className = "" }) {
  const searchParams = useSearchParams();
  const [value, setValue] = useState(
    searchParams.get(queryKey)?.toString() || "",
  );
  const router = useRouter();
  const pathname = usePathname();
  const timerId = useRef(null);

  function handleChange(val) {
    setValue(val);

    clearTimeout(timerId.current);
    timerId.current = setTimeout(() => {
      const params = new URLSearchParams(searchParams);
      if (val.trim()) {
        params.set(queryKey, val.trim());
      } else {
        params.delete(queryKey);
      }
      params.delete("pageNo");
      router.replace(`${pathname}?${params}`);
    }, 300);
  }

  return (
    <FormInput
      containerClassName={`max-w-md ${className}`}
      placeholder="Search by name or speciality..."
      startIcon={<Search />}
      value={value}
      onChange={(e) => handleChange(e.target.value)}
    />
  );
}

export default SearchBar;
