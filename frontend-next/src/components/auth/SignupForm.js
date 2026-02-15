"use client";

import { useState } from "react";
import RoleSelector from "./RoleSelector";
import FormInput from "../ui/FormInput";
import Button from "../ui/Button";
import { Eye, EyeOff, IdCard, Lock, Mail, Phone } from "lucide-react";

function SignupForm() {
  const [role, setRole] = useState("doctor");
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div>
      <RoleSelector className="my-8" value={role} setValue={setRole} />
      <form className="gap-form flex flex-col">
        <div className="gap-form grid md:grid-cols-2">
          <FormInput
            type="text"
            label="Full Name"
            autoComplete="name"
            placeholder="John Doe"
            startIcon={<IdCard />}
          />
          <FormInput
            type="tel"
            label="Phone"
            autoComplete="tel"
            placeholder="01X XXX XXXX"
            startIcon={<Phone />}
          />
        </div>
        <FormInput
          type="email"
          label="Email"
          autoComplete="email"
          startIcon={<Mail />}
        />
        <FormInput
          type={showPassword ? "text" : "password"}
          label="Password"
          autoComplete="new-password"
          startIcon={<Lock />}
          endIcon={
            showPassword ? (
              <Eye onClick={() => setShowPassword(false)} />
            ) : (
              <EyeOff onClick={() => setShowPassword(true)} />
            )
          }
        />
        <Button className="mt-6" type="submit">
          Sign Up
        </Button>
      </form>
    </div>
  );
}

export default SignupForm;
