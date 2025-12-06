import { configureStore } from "@reduxjs/toolkit";
import testSlice from "./test";

const store = configureStore({
  reducer: {
    test: testSlice,
  },
});

export { store };
