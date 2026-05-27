import React, { useState, useEffect } from 'react';
import {
    Box, Button, Card, CardContent, Grid, TextField, Typography,
    Dialog, DialogTitle, DialogContent, DialogActions, Select, MenuItem,
    FormControl, InputLabel, IconButton, CircularProgress
} from '@mui/material';
import { Add, Edit, Delete } from '@mui/icons-material';
import api from '../api/client';

interface Author { id: number; name: string; }
interface Category { id: number; name: string; }
interface Book { id: number; title: string; publicationYear: number; authorId: number | null; categoryIds: number[]; }

const BooksPage: React.FC = () => {
    const [books, setBooks] = useState<Book[]>([]);
    const [authors, setAuthors] = useState<Author[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState(true);
    const [open, setOpen] = useState(false);
    const [editingBook, setEditingBook] = useState<Book | null>(null);

    const [formData, setFormData] = useState({
        title: '',
        publicationYear: '',
        authorId: '',
        categoryIds: [] as number[]
    });
    const [filters, setFilters] = useState({ author: '', title: '', fromYear: '', toYear: '', categoryId: '' });

    const loadData = async () => {
        setLoading(true);
        try {
            const [booksRes, authorsRes, categoriesRes] = await Promise.all([
                api.get('/books/search/advanced/page?page=0&size=100'),
                api.get('/authors'),
                api.get('/categories')
            ]);
            setBooks(booksRes.data.content || []);
            setAuthors(authorsRes.data);
            setCategories(categoriesRes.data);
        } catch (err) { console.error(err); }
        finally { setLoading(false); }
    };

    useEffect(() => { loadData(); }, []);

    const handleSubmit = async () => {
        try {
            const data = {
                title: formData.title,
                publicationYear: formData.publicationYear ? parseInt(formData.publicationYear) : null,
                authorId: formData.authorId ? parseInt(formData.authorId) : null,
                categoryIds: formData.categoryIds
            };
            if (editingBook) await api.put(`/books/${editingBook.id}`, data);
            else await api.post('/books', data);
            handleClose();
            loadData();
        } catch (err) { alert('Ошибка сохранения'); }
    };

    const handleDelete = async (id: number) => {
        if (confirm('Удалить книгу?')) {
            await api.delete(`/books/${id}`);
            loadData();
        }
    };

    const handleEdit = (book: Book) => {
        setEditingBook(book);
        setFormData({
            title: book.title,
            publicationYear: book.publicationYear?.toString() || '',
            authorId: book.authorId?.toString() || '',
            categoryIds: book.categoryIds || []
        });
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setEditingBook(null);
        setFormData({ title: '', publicationYear: '', authorId: '', categoryIds: [] });
    };

    const applyFilters = async () => {
        let url = '/books/search/advanced/page?page=0&size=100';
        const params = new URLSearchParams();

        if (filters.author) params.append('author', filters.author);
        if (filters.title) params.append('title', filters.title);
        if (filters.fromYear) params.append('fromYear', filters.fromYear);
        if (filters.toYear) params.append('toYear', filters.toYear);

        if (filters.categoryId) {
            params.append('categoryId', String(filters.categoryId));
        }

        const finalUrl = params.toString() ? `${url}&${params.toString()}` : url;

        console.log("Фильтр URL:", finalUrl);  // для отладки

        const res = await api.get(finalUrl);
        setBooks(res.data.content || []);
    };

    const resetFilters = () => {
        setFilters({ author: '', title: '', fromYear: '', toYear: '', categoryId: '' });
        loadData();
    };

    if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;

    const getCategoryNames = (ids: number[]) => {
        if (!ids?.length) return '—';
        const uniqueIds = [...new Set(ids)];
        return uniqueIds.map(id => categories.find(c => c.id === id)?.name || id).join(', ');
    };

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4, flexWrap: 'wrap', gap: 2 }}>
                <Typography
                    variant="h4"
                    sx={{
                        fontWeight: 800,
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        WebkitBackgroundClip: 'text',
                        WebkitTextFillColor: 'transparent',
                    }}
                >
                    📖 Моя библиотека
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    sx={{
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        borderRadius: '30px',
                        px: 3,
                        transition: 'transform 0.2s',
                        '&:hover': {
                            transform: 'scale(1.02)',
                        }
                    }}
                    onClick={() => setOpen(true)}
                >
                    Добавить книгу
                </Button>
            </Box>

            <Card sx={{
                mb: 4,
                p: 3,
                borderRadius: '20px',
                background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)',
                boxShadow: '0 10px 40px rgba(0,0,0,0.05)'
            }}>
                <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
                    🔍 Расширенный поиск
                </Typography>
                <Grid container spacing={2}>
                    <Grid size={{ xs: 12, sm: 6, md: 2.4 }}>
                        <TextField label="Автор" fullWidth size="small" value={filters.author} onChange={e => setFilters({ ...filters, author: e.target.value })} />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 2.4 }}>
                        <TextField label="Название" fullWidth size="small" value={filters.title} onChange={e => setFilters({ ...filters, title: e.target.value })} />
                    </Grid>
                    <Grid size={{ xs: 6, md: 1.5 }}>
                        <TextField label="Год от" type="number" fullWidth size="small" value={filters.fromYear} onChange={e => setFilters({ ...filters, fromYear: e.target.value })} />
                    </Grid>
                    <Grid size={{ xs: 6, md: 1.5 }}>
                        <TextField label="Год до" type="number" fullWidth size="small" value={filters.toYear} onChange={e => setFilters({ ...filters, toYear: e.target.value })} />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6, md: 2.4 }}>
                        <FormControl fullWidth size="small">
                            <InputLabel id="filter-category-label">Категория</InputLabel>
                            <Select
                                labelId="filter-category-label"
                                label="Категория"
                                value={filters.categoryId}
                                onChange={e => setFilters({ ...filters, categoryId: e.target.value })}
                            >
                                <MenuItem value="">Все</MenuItem>
                                {categories.map(c => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid size={{ xs: 12, md: 2.4 }} sx={{ display: 'flex', gap: 1 }}>
                        <Button variant="contained" onClick={applyFilters}>Применить</Button>
                        <Button variant="outlined" onClick={resetFilters}>Сбросить</Button>
                    </Grid>
                </Grid>
            </Card>

            <Grid container spacing={3}>
                {books.map(book => (
                    <Grid size={{ xs: 12, sm: 6, md: 4 }} key={book.id}>
                        <Card sx={{
                            height: '100%',
                            display: 'flex',
                            flexDirection: 'column',
                            borderRadius: '20px',
                            transition: 'all 0.3s ease',
                            '&:hover': {
                                transform: 'translateY(-8px)',
                                boxShadow: '0 20px 40px rgba(0,0,0,0.1)',
                            }
                        }}>
                            <CardContent sx={{ flexGrow: 1, p: 3 }}>
                                <Typography variant="h6" gutterBottom sx={{ fontWeight: 700 }}>
                                    {book.title}
                                </Typography>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                                    <Typography color="text.secondary" variant="body2">
                                        📅 Год: {book.publicationYear || '—'}
                                    </Typography>
                                    <Typography color="text.secondary" variant="body2">
                                        🏷️ Категории: {getCategoryNames(book.categoryIds)}
                                    </Typography>
                                    <Typography color="text.secondary" variant="body2">
                                        🔗 Связанный автор: {authors.find(a => a.id === book.authorId)?.name || '—'}
                                    </Typography>
                                </Box>
                            </CardContent>
                            <Box sx={{ p: 2, pt: 0, display: 'flex', gap: 1, borderTop: '1px solid #eee', mt: 'auto' }}>
                                <IconButton
                                    size="small"
                                    sx={{
                                        color: '#667eea',
                                        '&:hover': { backgroundColor: 'rgba(102,126,234,0.1)' }
                                    }}
                                    onClick={() => handleEdit(book)}
                                >
                                    <Edit />
                                </IconButton>
                                <IconButton
                                    size="small"
                                    sx={{
                                        color: '#e53e3e',
                                        '&:hover': { backgroundColor: 'rgba(229,62,62,0.1)' }
                                    }}
                                    onClick={() => handleDelete(book.id)}
                                >
                                    <Delete />
                                </IconButton>
                            </Box>
                        </Card>
                    </Grid>
                ))}
                {books.length === 0 && (
                    <Grid size={12}>
                        <Typography sx={{ textAlign: 'center', py: 4, color: 'text.secondary' }}>
                            Книг не найдено
                        </Typography>
                    </Grid>
                )}
            </Grid>

            <Dialog
                open={open}
                onClose={handleClose}
                maxWidth="sm"
                fullWidth
                sx={{
                    '& .MuiPaper-root': { borderRadius: '12px' }
                }}
            >
                <DialogTitle sx={{ fontWeight: 700 }}>
                    {editingBook ? '✏️ Редактировать книгу' : '➕ Добавить книгу'}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5, mt: 1 }}>
                        <TextField
                            label="Название"
                            fullWidth
                            value={formData.title}
                            onChange={e => setFormData({ ...formData, title: e.target.value })}
                        />
                        <TextField
                            label="Год издания"
                            type="number"
                            fullWidth
                            value={formData.publicationYear}
                            onChange={e => setFormData({ ...formData, publicationYear: e.target.value })}
                        />
                        <FormControl fullWidth>
                            <InputLabel id="form-author-label">Связанный автор </InputLabel>
                            <Select
                                labelId="form-author-label"
                                label="Связанный автор"
                                value={formData.authorId}
                                onChange={e => setFormData({ ...formData, authorId: e.target.value })}
                            >
                                <MenuItem value="">Не выбран</MenuItem>
                                {authors.map(a => <MenuItem key={a.id} value={a.id}>{a.name}</MenuItem>)}
                            </Select>
                        </FormControl>
                        <FormControl fullWidth>
                            <InputLabel id="form-categories-label">Категории </InputLabel>
                            <Select
                                multiple
                                labelId="form-categories-label"
                                label="Категории"
                                value={formData.categoryIds}
                                onChange={e => setFormData({ ...formData, categoryIds: typeof e.target.value === 'string' ? [] : e.target.value })}
                                renderValue={(selected) => selected.map(id => categories.find(c => c.id === id)?.name).join(', ')}
                            >
                                {categories.map(c => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
                            </Select>
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 3 }}>
                    <Button onClick={handleClose}>Отмена</Button>
                    <Button
                        onClick={handleSubmit}
                        variant="contained"
                        sx={{ px: 4, fontWeight: 600, borderRadius: '6px', boxShadow: 'none' }}
                    >
                        Сохранить
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default BooksPage;