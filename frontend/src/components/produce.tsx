"use client";

import { useState, useEffect } from "react";
import { Button } from "@mui/material";
import SendIcon from '@mui/icons-material/Send';

export default function Produce() {
  const [msg, setMsg] = useState();

  // pingしてmsgに設定
  function handleProduce() {
    setMsg(Object());
    fetch("/produce", {
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
        <Button variant="contained" onClick={handleProduce} endIcon={<SendIcon />}>
          /produce
        </Button>
        <pre>{JSON.stringify(msg, null, 2)}</pre>
      </div>
    </>
  );
}
