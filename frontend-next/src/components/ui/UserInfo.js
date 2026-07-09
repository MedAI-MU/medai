import { getUser } from "@/services/server/auth";
import ErrorMessage from "./ErrorMessage";
import defaultAvatar from "@/assets/default-user.jpg";
import Image from "next/image";

async function UserInfo() {
  let user;
  try {
    user = await getUser();
  } catch (err) {
    return <ErrorMessage message="Failed to load user data" withBg />;
  }

  return (
    <div className="group flex cursor-pointer items-center gap-3">
      <div className="hidden text-right md:block">
        <p className="text-text-base group-hover:text-primary text-sm font-semibold transition-colors">
          {user?.name}
        </p>
        <p className="text-text-subtle text-xs">
          <span className="capitalize">{user?.role}</span> ID: #{user?.id}
        </p>
      </div>

      {/* Avatar */}
      <div className="border-surface bg-primary/20 size-9 shrink-0 overflow-hidden rounded-full border-2 md:size-10">
        <Image
          alt="User avatar"
          className="h-full w-full object-cover"
          src={user?.avatar || defaultAvatar}
        />
      </div>
    </div>
  );
}

export default UserInfo;
