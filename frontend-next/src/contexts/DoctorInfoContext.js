"use client";

import { createContext, useContext, useState } from "react";

const DoctorInfoContext = createContext();

function DoctorInfoProvider({ doctorInfo = {}, children }) {
  const [doctor, setDoctor] = useState(doctorInfo);
  return (
    <DoctorInfoContext.Provider value={{ doctor, setDoctor }}>
      {children}
    </DoctorInfoContext.Provider>
  );
}

function useDoctorInfo() {
  const context = useContext(DoctorInfoContext);
  if (context === undefined)
    throw new Error("useDoctorInfo is used outside DoctorInfoProvider");

  return context;
}

export { DoctorInfoProvider, useDoctorInfo };
