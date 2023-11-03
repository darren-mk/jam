<script>
  let n = 0;
  function inc() {
    n = n + 1 ; }
</script>

<h2>Counted: {n}</h2>
<button on:click={inc}>
  add up!
</button>
