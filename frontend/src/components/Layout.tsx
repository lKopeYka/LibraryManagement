import React from 'react';
import { AppBar, Container, Toolbar, Typography, Button, Box } from '@mui/material';
import { Link, Outlet } from 'react-router-dom';
import LibraryBooksIcon from '@mui/icons-material/LibraryBooks';
import PeopleIcon from '@mui/icons-material/People';

const Layout: React.FC = () => {
    return (
        <>
            <AppBar
                position="sticky"
                elevation={0}
                sx={{
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                }}
            >
                <Toolbar>
                    <LibraryBooksIcon sx={{ mr: 2 }} />
                    <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>
                        Библиотека
                    </Typography>

                    <Button
                        color="inherit"
                        component={Link}
                        to="/"
                        sx={{
                            mx: 1,
                            '&:hover': {
                                backgroundColor: 'rgba(255,255,255,0.1)',
                                transform: 'translateY(-2px)',
                            },
                            transition: 'all 0.2s'
                        }}
                    >
                        <LibraryBooksIcon sx={{ mr: 0.5 }} fontSize="small" />
                        Книги
                    </Button>

                    {/* Кнопка Авторы */}
                    <Button
                        color="inherit"
                        component={Link}
                        to="/authors"
                        sx={{
                            mx: 1,
                            '&:hover': {
                                backgroundColor: 'rgba(255,255,255,0.1)',
                                transform: 'translateY(-2px)',
                            },
                            transition: 'all 0.2s'
                        }}
                    >
                        <PeopleIcon sx={{ mr: 0.5 }} fontSize="small" />
                        Авторы
                    </Button>
                </Toolbar>
            </AppBar>

            <Box sx={{ background: '#f5f7fb', minHeight: '100vh' }}>
                <Container maxWidth="xl" sx={{ py: 4 }}>
                    <Outlet />
                </Container>
            </Box>
        </>
    );
};

export default Layout;