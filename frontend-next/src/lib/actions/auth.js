import { validateSchema } from "../utils/validateSchema";
import { loginSchema, signupSchema } from "../zod/schemas";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export async function signupAction(userData) {
  // 1) Validation to ensure integrity
  const validation = validateSchema(signupSchema, userData);
  if (!validation.success)
    return {
      success: false,
      type: "validation",
      fieldErrors: validation.fieldErrors,
    };

  // 2) Send data to backend
  try {
    const res = await fetch(`${API_BASE_URL}/api/users`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
      credentials: "include",
    });

    // 3) ✅ All is good
    if (res.status === 201) {
      // Quick login
      const loginRes = await loginAction({
        email: userData?.email,
        password: userData?.password,
      });

      if (!loginRes.success) throw new Error("Error during direct login");

      return {
        success: true,
        type: "backend",
        message: "Account created successfully.",
        user: loginRes.user,
      };
    }

    const data = await res.json();

    // 4a) Backend validation error
    if (res?.status === 400) {
      console.error("Backend validation error");

      // unify error formate => {email: "error"}
      const fieldErrors = {};
      const backendErrors = data?.validationErrors;

      for (const field of backendErrors) {
        if (!fieldErrors[field?.field])
          fieldErrors[field?.field] = field?.error;
      }

      return {
        success: false,
        type: "validation",
        fieldErrors,
        statusCode: 400,
      };
    }

    // 4b) Backend user already exists
    if (res?.status === 409)
      return {
        success: false,
        type: "backend",
        message: data?.message,
        statusCode: 409,
      };

    // 5) Network/server errors + any other error
    if (!res.ok || res.status !== 201) throw new Error(data?.message);
  } catch (err) {
    console.error(err?.message || "Network error, try again");

    return {
      success: false,
      type: "system",
      message: "Network/server error, try again",
      statusCode: 500,
    };
  }
}

export async function loginAction(userData) {
  // 1) Validation to ensure integrity
  const validation = validateSchema(loginSchema, userData);
  if (!validation.success)
    return {
      success: false,
      type: "validation",
      fieldErrors: validation.fieldErrors,
    };

  // 2) Send data to backend
  try {
    const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(userData),
      credentials: "include",
    });

    // 3a) Invalid email or password
    if (res?.status === 400)
      return {
        success: false,
        type: "validation",
        statusCode: 400,
        message: "Invalid Email or Password",
      };

    // 3b) Network/server errors + any other error
    if (res.status !== 200 || !res.ok) throw new Error("login error thrown");

    // 4) ✅ All is good
    if (res.status === 200) {
      const data = await res.json();
      return {
        success: true,
        type: "backend",
        message: "You have logged in successfully.",
        user: data, // user info
      };
    }
  } catch (err) {
    console.error(err?.message || "Network error, try again");

    return {
      success: false,
      type: "system",
      message: "Network/server error, try again",
      statusCode: 500,
    };
  }
}

export async function refreshToken({ headers = {} }) {
  try {
    const res = await fetch(`${API_BASE_URL}/api/auth/refresh-token`, {
      method: "POST",
      credentials: "include",
      headers: {
        ...headers,
      },
    });

    if (res.status === 200)
      return {
        success: true,
      };
    return { success: false };
  } catch {
    return { success: false };
  }
}

// {
//   success: false,
//   message: 'Validation failed',
//   validationErrors: [
//     { field: 'name', error: 'name should not be empty' },
//     {
//       field: 'name',
//       error: 'name must be longer than or equal to 5 characters'
//     }
//   ]
// }

// {
//   message: 'Phone number or email already in use',
//   error: 'Conflict',
//   statusCode: 409
// }

// {
//   name: [ 'Name must be between 5 and 100 characters' ],
//   password: [ 'Password must be at least 6 characters' ]
// }

// sent to client to view
// {
//   name: 'name should not be empty',
//   password: 'password should not be empty'
// }

/* Patient info */

// {
//     "createdAt": "2026-02-22T11:56:48.433Z",
//     "updatedAt": "2026-02-22T11:56:48.433Z",
//     "userId": 1,
//     "user": {
//         "createdAt": "2026-02-22T11:56:48.388Z",
//         "updatedAt": "2026-02-22T11:56:48.388Z",
//         "id": 1,
//         "name": "Abdo Ghozal",
//         "gender": null,
//         "role": "patient"
//     },
//     "height": null,
//     "weight": null,
//     "bloodType": null,
//     "maritalStatus": null,
//     "allergies": [],
//     "chronicDiseases": [],
//     "surgeries": [],
//     "familyHistories": [],
//     "emergencyContacts": []
// }

/* Doctor info */
// {
//     "createdAt": "2026-02-23T20:01:17.427Z",
//     "updatedAt": "2026-02-23T20:01:17.427Z",
//     "userId": 3,
//     "user": {
//         "createdAt": "2026-02-23T20:01:17.380Z",
//         "updatedAt": "2026-02-23T20:01:17.380Z",
//         "id": 3,
//         "name": "doctor ghozal",
//         "gender": null,
//         "role": "doctor"
//     },
//     "specialities": []
// }
