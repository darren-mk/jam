import styles from './App.module.css';
import Counter from './Counter.jsx';

function App() {
    return (
        <div class={styles.App}>
            <header class={styles.header}>
                <Counter />
            </header>
        </div>
    );
}

export default App;
