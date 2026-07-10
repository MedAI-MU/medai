import { getUser } from "@/services/server/auth";

import Heading from "@/components/ui/Heading";
import ProfileView from "@/components/profile/ProfileView";
import EditProfile from "@/components/profile/EditProfile";
import { redirect } from "next/navigation";

export default async function ProfilePage() {
  let user;
  try {
    user = await getUser();
  } catch (err) {
    if (err?.statusCode === 401) redirect("/");
  }

  return (
    <div className="space-y-8">
      <Heading title="Profile" subtitle="Your account details.">
        <EditProfile user={user} />
      </Heading>
      <ProfileView user={user} />
    </div>
  );
}
