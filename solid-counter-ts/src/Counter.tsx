import { createSignal } from "solid-js";

const Counter = () => {
  const [getCount, setCount] = createSignal(0);
  const incFn = () => setCount(getCount() + 1);
  const decFn = () => setCount(getCount() - 1);
  const ui =
    <div>
      <h1>{getCount()}</h1>
      <button onClick={incFn}>+</button>
      <button onClick={decFn}>-</button>
    </div>
  return ui
}

export default Counter;
