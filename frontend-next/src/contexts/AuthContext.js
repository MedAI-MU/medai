"use client";

import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

function AuthProvider({ CurrentUser = null, children }) {
  const [user, setUser] = useState(CurrentUser);

  return (
    <AuthContext.Provider value={{ user, setUser }}>
      {children}
    </AuthContext.Provider>
  );
}

function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined)
    throw new Error("useAuth is used outside AuthProvider");

  return context;
}

export { AuthProvider, useAuth };
