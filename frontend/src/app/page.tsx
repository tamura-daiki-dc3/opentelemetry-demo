"use client";

import Greeting from '../components/greeting';
import Ping from '../components/ping';

export default function Home() {
  return (
    <div>
      <h1>OpenTelemetryを始めてみよう！</h1>


      <h2>Javaコースの方用</h2>
      <p>ボタンを押下すると、<pre>GET /greeting</pre>リクエストをJavaコンテナへ送信します</p>
      <Greeting></Greeting>

      <hr />

      <h2>Go言語コースの方用</h2>
      <p>ボタンを押下すると、<pre>GET /ping</pre>リクエストをGoコンテナへ送信します</p>
      <Ping></Ping>
    </div>
  )
}
