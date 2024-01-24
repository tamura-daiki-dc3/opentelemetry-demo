"use client";

import { useState, useEffect } from "react";
import { Button } from "@mui/material";
import SendIcon from '@mui/icons-material/Send';

export default function Greeting() {
  const [msg, setMsg] = useState();

  // pingしてmsgに設定
  function handlePing() {
    setMsg(Object());
    fetch("/greeting", {
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((res) => {
        console.log(res);
        res.json().then((j) => {
          setMsg(j);
        });
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  }

  return (
    <>
      <div>
        <Button variant="contained" onClick={handlePing} endIcon={<SendIcon />}>
          /greeting
        </Button>
        <pre>{JSON.stringify(msg, null, 2)}</pre>
      </div>
    </>
  );
}
