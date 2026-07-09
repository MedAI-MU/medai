import { getUser } from "@/services/server/auth";
import ErrorMessage from "./ErrorMessage";

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
        <img
          alt="User avatar"
          className="h-full w-full object-cover"
          src={
            user?.avatar ||
            "https://lh3.googleusercontent.com/aida-public/AB6AXuAOcWE1M30lFGq-GjI6ngcTxkRqQomwggB3oNi-WAZwrzRGkM02YAQEBShWWHPvoAYSzlyLA7a96eeDZlqr5D7pLCRXdvR3dbk8RcLZvIrgQ4iTy4lEt-tLgHaMfhTmqk4C4o-wEXMINTefQgYrCRFfHEvbHY6EHwqYR_S4cDg-zGkgF9grT_ds4MBR1jlKH3Nrv_ZI0N1yjGm5dJLqO_GlbVfECJO-4M8dV136Jq06XDCyjwyxIsUllUCeBNiSGI2yCi-qXDC0UoM"
          }
        />
      </div>
    </div>
  );
}

export default UserInfo;
