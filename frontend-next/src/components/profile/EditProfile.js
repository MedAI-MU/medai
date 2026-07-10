"use client";

import { Pencil } from "lucide-react";
import Button from "../ui/Button";
import FormSheet from "../ui/FormSheet";
import ProfileEditForm from "./ProfileEditForm";

function EditProfile({ user }) {
  return (
    <FormSheet
      title="Edit Profile"
      description="Update your personal information."
      form={<ProfileEditForm user={user} />}
    >
      <Button variation="primary" startIcon={<Pencil size={16} />}>
        Edit Profile
      </Button>
    </FormSheet>
  );
}

export default EditProfile;
