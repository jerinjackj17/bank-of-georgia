import './App.css';
import { Routes, Route } from 'react-router-dom';

function App() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <Routes>
        <Route path="/" element={<h1 className="text-3xl font-bold text-blue-600">Bank of Georgia Frontend</h1>} />
      </Routes>
    </div>
  );
}

export default App;