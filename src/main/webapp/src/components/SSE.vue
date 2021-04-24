<template>
  <div>
    <p v-for="(message, index) in messages" :key="`msg-${index}`">{{ message }}</p>
  </div>
</template>

<script>
// We store the reference to the SSE object out here
// so we can access it from other methods
let msgServer;

export default {
  name: 'SSE',
  data() {
    return {
      messages: [],
    };
  },
  props: {
    sseUrl: String
  },
  mounted() {
    (async () => {
      try {
        // Store SSE object at a higher scope
        msgServer = await this.$sse(`${this.sseUrl}`, { format: 'plain'}); // omit for no format pre-processing

        // Catch any errors (ie. lost connections, etc.)
        msgServer.onError(e => {
          console.error('lost connection; giving up!', e);

          // If you don't want SSE to automatically reconnect (if possible),
          // then uncomment the following line:
          msgServer.close();
        });

        // Listen for messages without a specified event
        msgServer.subscribe('', (data) => {
          console.warn('Received a message w/o an event!', data);
          this.messages.push(data);
        });

        // Unsubscribes from event-less messages after 30 seconds
        setTimeout(() => {
          msgServer.unsubscribe('');

          console.log('Stopped listening to event-less messages!');
        }, 30000);
      } catch (err) {
        // When this error is caught, it means the initial connection to the
        // events server failed.  No automatic attempts to reconnect will be made.
        console.error('Failed to connect to server', err);
      }
    })();
  },
  beforeDestroy() {
    // Make sure to close the connection with the events server
    // when the component is destroyed, or we'll have ghost connections!
    msgServer.close();
  },
};
</script>