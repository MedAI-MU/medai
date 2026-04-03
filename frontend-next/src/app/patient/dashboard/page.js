import MedicalReports from "../../../components/medical-reports/MedicalReports";
import Container from "../../../components/ui/Container";
import { getUserFromToken } from "@/lib/auth";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export default async function PatientDashboard() {
  const user = await getUserFromToken();
  if (!user || user.role !== 'patient') {
    redirect('/auth/login');
  }

  const cookieStore = await cookies();
  const token = cookieStore.get("Authentication")?.value;

  return (
    <Container className="py-8">
      <h1 className="text-3xl font-bold text-blue-900 mb-6">Patient Dashboard</h1>
      <div className="bg-white p-6 rounded-lg shadow-md mb-6">
        <h2 className="text-xl font-semibold mb-2">Welcome Back!</h2>
        <p className="text-gray-600">View your medical history and AI-generated scan reports below.</p>
      </div>

      <MedicalReports patientId={user.sub} token={token} />
    </Container>
  );
}
