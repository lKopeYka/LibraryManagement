import { createBrowserRouter } from 'react-router-dom';
import Layout from './components/Layout';
import BooksPage from './pages/BooksPage';
import AuthorsPage from './pages/AuthorsPage';

export const router = createBrowserRouter([
    {
        path: '/',
        element: <Layout />,
        children: [
            { index: true, element: <BooksPage /> },
            { path: 'authors', element: <AuthorsPage /> },
        ],
    },
]);