"use client";

import { useState, useEffect } from "react";
import Button from "../ui/Button";
import TextArea from "../ui/TextArea";
import ErrorMessage from "../ui/ErrorMessage";

export default function MedicalReports({ patientId, token }) {
  const [reports, setReports] = useState([]);
  const [scanData, setScanData] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchReports();
  }, [patientId]);

  const fetchReports = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:3000/patients/${patientId}/reports`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) throw new Error("Failed to fetch reports");
      const data = await response.json();
      setReports(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateReport = async (e) => {
    e.preventDefault();
    if (!scanData.trim()) return;

    try {
      setLoading(true);
      setError(null);
      const response = await fetch(`http://localhost:3000/patients/${patientId}/reports`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ scanData }),
      });

      if (!response.ok) throw new Error("Failed to generate report");

      const newReport = await response.json();
      setReports([newReport, ...reports]);
      setScanData("");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md mt-6">
      <h2 className="text-2xl font-bold mb-4 text-blue-900">AI Medical Reports</h2>

      <form onSubmit={handleGenerateReport} className="mb-8 p-4 bg-blue-50 rounded-lg">
        <h3 className="text-lg font-semibold mb-2 text-blue-800">Generate New Report</h3>
        <p className="text-sm text-gray-600 mb-4">
          Enter scan details or image URL to generate an AI medical report.
        </p>
        <TextArea
          value={scanData}
          onChange={(e) => setScanData(e.target.value)}
          placeholder="e.g., https://example.com/scan.jpg or describing text..."
          className="w-full mb-4"
          rows={3}
        />
        <Button type="submit" disabled={loading || !scanData.trim()} className="w-full sm:w-auto">
          {loading ? "Generating..." : "Generate AI Report"}
        </Button>
        {error && <ErrorMessage message={error} className="mt-4" />}
      </form>

      <div className="space-y-4">
        <h3 className="text-lg font-semibold text-blue-800">Past Reports</h3>
        {reports.length === 0 ? (
          <p className="text-gray-500">No medical reports found.</p>
        ) : (
          reports.map((report) => (
            <div key={report.id} className="border border-gray-200 rounded-lg p-4">
              <div className="flex justify-between items-start mb-2">
                <span className="text-sm text-gray-500">
                  {new Date(report.createdAt).toLocaleDateString()}
                </span>
                <span className="px-2 py-1 bg-green-100 text-green-800 text-xs rounded-full">
                  {report.status}
                </span>
              </div>
              {report.scanImageUrl && (
                <div className="mb-2 text-sm text-blue-600 break-all">
                  Scan Ref: {report.scanImageUrl}
                </div>
              )}
              <div className="mt-2 text-gray-700 whitespace-pre-wrap">
                {report.generatedReport}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
