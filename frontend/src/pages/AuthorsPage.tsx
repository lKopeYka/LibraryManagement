import React, { useState, useEffect } from 'react';
import { Box, Button, Card, CardContent, Grid, TextField, Typography, Dialog, DialogTitle, DialogContent, DialogActions, IconButton, CircularProgress } from '@mui/material';
import { Add, Edit, Delete } from '@mui/icons-material';
import api from '../api/client';

interface Author { id: number; name: string; birthDate: string | null; birthCountry: string | null; biography: string | null; }

const AuthorsPage: React.FC = () => {
    const [authors, setAuthors] = useState<Author[]>([]);
    const [loading, setLoading] = useState(true);
    const [open, setOpen] = useState(false);
    const [editingAuthor, setEditingAuthor] = useState<Author | null>(null);
    const [formData, setFormData] = useState({ name: '', birthDate: '', birthCountry: '', biography: '' });

    const loadAuthors = async () => {
        setLoading(true);
        try { const res = await api.get('/authors'); setAuthors(res.data); }
        catch (err) { console.error(err); }
        finally { setLoading(false); }
    };
    useEffect(() => { loadAuthors(); }, []);

    const handleSubmit = async () => {
        try {
            if (editingAuthor) await api.put(`/authors/${editingAuthor.id}`, formData);
            else await api.post('/authors', formData);
            handleClose();
            loadAuthors();
        } catch (err) { alert('Ошибка сохранения'); }
    };

    const handleDelete = async (id: number) => {
        if (confirm('Удалить автора?')) { await api.delete(`/authors/${id}`); loadAuthors(); }
    };

    const handleEdit = (author: Author) => {
        setEditingAuthor(author);
        setFormData({ name: author.name, birthDate: author.birthDate || '', birthCountry: author.birthCountry || '', biography: author.biography || '' });
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setEditingAuthor(null);
        setFormData({ name: '', birthDate: '', birthCountry: '', biography: '' });
    };

    if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}><CircularProgress /></Box>;

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
                    👨‍💼 Авторы
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
                    Добавить автора
                </Button>
            </Box>

            <Grid container spacing={3}>
                {authors.map(author => (
                    <Grid size={{ xs: 12, sm: 6, md: 4 }} key={author.id}>
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
                                    {author.name}
                                </Typography>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                                    <Typography color="text.secondary" variant="body2">
                                        🌍 {author.birthCountry || '—'}, {author.birthDate || '—'}
                                    </Typography>
                                    <Typography color="text.secondary" variant="body2" sx={{ mt: 1 }}>
                                        {author.biography ? author.biography.slice(0, 100) : '—'}
                                        {author.biography && author.biography.length > 100 ? '...' : ''}
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
                                    onClick={() => handleEdit(author)}
                                >
                                    <Edit />
                                </IconButton>
                                <IconButton
                                    size="small"
                                    sx={{
                                        color: '#e53e3e',
                                        '&:hover': { backgroundColor: 'rgba(229,62,62,0.1)' }
                                    }}
                                    onClick={() => handleDelete(author.id)}
                                >
                                    <Delete />
                                </IconButton>
                            </Box>
                        </Card>
                    </Grid>
                ))}
                {authors.length === 0 && (
                    <Grid size={12}>
                        <Typography sx={{ textAlign: 'center', py: 4, color: 'text.secondary' }}>
                            Авторов не найдено
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
                <DialogTitle sx={{ fontWeight: 700, pb: 1 }}>
                    {editingAuthor ? '✏️ Редактировать автора' : '➕ Добавить автора'}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5, mt: 1 }}>
                        <TextField
                            label="ФИО"
                            fullWidth
                            value={formData.name}
                            onChange={e => setFormData({ ...formData, name: e.target.value })}
                        />
                        <TextField
                            label="Дата рождения"
                            fullWidth
                            placeholder="YYYY-MM-DD"
                            value={formData.birthDate}
                            onChange={e => setFormData({ ...formData, birthDate: e.target.value })}
                        />
                        <TextField
                            label="Страна"
                            fullWidth
                            value={formData.birthCountry}
                            onChange={e => setFormData({ ...formData, birthCountry: e.target.value })}
                        />
                        <TextField
                            label="Биография"
                            multiline
                            rows={3}
                            fullWidth
                            value={formData.biography}
                            onChange={e => setFormData({ ...formData, biography: e.target.value })}
                        />
                    </Box>
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 3 }}>
                    <Button onClick={handleClose} sx={{ fontWeight: 600 }}>Отмена</Button>
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

export default AuthorsPage;