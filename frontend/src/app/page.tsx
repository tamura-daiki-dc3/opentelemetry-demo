"use client";

import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Produce from "../components/produce";
import Greeting from "../components/greeting";
import Ping from "../components/ping";

export default function Home() {
  return (
    <Container maxWidth="lg">
      <div>
        <Typography variant="h3" component="h3">
          OpenTelemetryを始めてみよう
        </Typography>

        <hr />

        <Typography variant="h4" component="h4">
          Javaコースの方用
        </Typography>
        <p>
          ボタンを押下すると、<pre>GET /greeting</pre>リクエストをJavaコンテナへ送信します
        </p>
        <Greeting></Greeting>

        <hr />

        <Typography variant="h4" component="h4">
          Go言語コースの方用
        </Typography>
        <p>
          ボタンを押下すると、<pre>GET /ping</pre>リクエストをGoコンテナへ送信します
        </p>
        <Ping></Ping>

        <hr />

        <Typography variant="h4" component="h4">
          Kafkaへメッセージ送信
        </Typography>
        <p>
          ボタンを押下すると、<pre>GET /produce</pre>リクエストをGoコンテナ <pre>golang-kafka-producer</pre>へ送信します
        </p>
        <Produce></Produce>
      </div>
    </Container>
  );
}
