"use client";

import { useState, useEffect } from "react";

export default function Ping() {

  const [msg, setMsg] = useState();

  // pingしてmsgに設定
  function handlePing() {
    setMsg(Object());
    fetch("/ping", {
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
        <button onClick={handlePing}>/ping</button>
        <pre>{JSON.stringify(msg, null, 2)}</pre>
      </div>
    </>
  );
}
