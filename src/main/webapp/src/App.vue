<template>
  <div id="app">
    <img alt="Quarkus logo" src="https://quarkus.io/assets/images/quarkus_logo_horizontal_rgb_600px_reverse.png">
    <h4>Welcome to a full stack all reactive application built with Quarkus, Vuejs and gRPC</h4>
    <p>In few seconds, you will see messages flowing below...</p>
    <p>The volume (max 100) and the pace (1 msg/sec) of the backend message stream is controlled by gRPC server.</p>
    <p>The frontend will close the server stream after 30 seconds.</p>
    <SSE sseUrl="/hello/multi/Jeslyn"/>
  </div>
</template>

<script>
import SSE from './components/SSE.vue'

export default {
  name: 'App',
  components: {
    SSE
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

img {
  background-color: black;
}
</style>
